import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import { NGXLogger } from 'ngx-logger';

import TestUtils from '@core/test-utils';
import { BaseService } from '../base.service';

interface Entity {
  id?: number;
  name: string;
}

const NOT_FOUND = {
  status: 404,
  statusText: 'Not Found'
};

describe('BaseService', () => {
  // Mock logger
  const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

  // Allow http call mocking
  let http: HttpClient;
  let httpMock: HttpTestingController;

  let service: BaseService<Entity>;

  // We store the service and the httpmock in variables we can access in every test and gets instanciated every time before a test runs
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);

    // We need to create de BaseService instance owrself as Angular does not know how to create it
    service = new BaseService(loggerSpy, http, '/entities');
  });

  afterEach(() => {
    // then: verify that there are not pending http calls
    httpMock.verify();
  });

  it('create() should POST and return data', () => {
    // given: a new Entity
    const newEntity: Entity = { name: 'Erik' } as Entity;
    // when: create method called
    service.create(newEntity).subscribe((res) => {
      // then: a new entity is created with the given name
      expect(res.name).toEqual('Erik');
    });

    // then: the right backend API is called
    const req = httpMock.expectOne('http://turnaround-server:8080/entities', 'GET to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('POST');

    // “fire” the request with the mocked result
    req.flush({ id: 99, name: 'Erik' });
  });

  it('update() should PUT and return data', () => {
    // given: an existing Entity
    const existingEntity = {
      id: 1,
      name: 'George'
    };
    // when: entity udpated and update method called
    const updatedEntity = Object.create(existingEntity);
    updatedEntity.name = 'Erik';
    service.update(existingEntity, existingEntity.id).subscribe((res) => {
      // then: the entity is updated with the given values
      expect(res).toEqual(updatedEntity);
    });

    // then: the right backend API is called
    const req = httpMock.expectOne('http://turnaround-server:8080/entities/1', 'PUT to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('PUT');

    // “fire” the request with the mocked result
    req.flush(updatedEntity);
  });

  it('update() non existing entity should throw error', () => {
    // given: a non existing Entity
    const nonExistingEntity = {
      id: 99,
      name: 'George'
    };
    // when: update method called
    service.update(nonExistingEntity, nonExistingEntity.id).subscribe(
      () => {
        fail('Expected error');
      },
      (error: HttpErrorResponse) => {
        // then: a 404 status should be returned
        expect(error.status).toEqual(404);
      }
    );

    // then: the right backend API is called
    const req = httpMock.expectOne('http://turnaround-server:8080/entities/99', 'PUT to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('PUT');

    // "fire" the request with not found result
    req.flush(null, NOT_FOUND);
  });

  it('delete() should DELETE exiting entity', () => {
    // given: existing entity id
    const id = 1;
    // when: delete method called with given id
    service.delete(id).subscribe((res) => {
      // then: the entity is deleted and nothing returned
      expect(res).toEqual(null);
    });

    // then: the right backend API is called
    const req = httpMock.expectOne('http://turnaround-server:8080/entities/1', 'DELETE to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('DELETE');

    // "fire" the request with the modcked result
    req.flush(null);
  });

  it('delete() not existing entity should throw error', () => {
    // given: no existing entity id
    const id = 1;
    // when: delete method called with given id
    service.delete(id).subscribe(
      () => {
        fail('Expected error');
      },
      (error) => {
        // then: a 404 status should be returned
        expect(error.status).toEqual(404);
      }
    );

    // then: the right backend API is called
    const req = httpMock.expectOne('http://turnaround-server:8080/entities/1', 'DELETE to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('DELETE');

    // "fire" the request with not found result
    req.flush('', NOT_FOUND);
  });

  it('findAll() should return data', () => {
    // given: existing entities
    const dummyEntityList = [
      { id: 1, name: 'George' },
      { id: 2, name: 'Janet' },
      { id: 3, name: 'Emma' },
    ];
    // when: findAll method called
    service.findAll().subscribe((res) => {
      // then: the existing entity list is returned
      expect(res).toEqual(dummyEntityList);
    });

    // then: the right backend API is called
    const req = httpMock.expectOne('http://turnaround-server:8080/entities', 'GET to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('GET');

    // "fire" the request with the modcked result
    req.flush(dummyEntityList);
  });

  it('getOne() should return data', () => {
    // given: existing entity
    const dummyEntity = {
      id: 1,
      name: 'George'
    };
    // when: findOne method called with existing entity id
    service.getOne(dummyEntity.id).subscribe((res) => {
      // then: the existing entity is returned
      expect(res).toEqual(dummyEntity);
    });

    // then: the right backend API is called
    const req = httpMock.expectOne('http://turnaround-server:8080/entities/1', 'GET to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('GET');

    // "fire" the request with the modcked result
    req.flush(dummyEntity);
  });

  it('getOne() of not existing entity should throw error', () => {
    // given: no existing entity id
    const id = 99;
    // when: findOne method called with that id
    service.getOne(id).subscribe(
      () => {
        fail('Expected error');
      },
      (error) => {
        // then: a 404 status should be returned
        expect(error.status).toEqual(404);
      }
    );

    // then: the right backend API is called
    const req = httpMock.expectOne('http://turnaround-server:8080/entities/99', 'GET to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('GET');

    // "fire" the request with not found result
    req.flush('', NOT_FOUND);
  });
});
