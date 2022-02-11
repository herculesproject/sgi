package org.crue.hercules.sgi.com.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.com.dto.EmailInput;
import org.crue.hercules.sgi.com.dto.EmailOutput;
import org.crue.hercules.sgi.com.dto.EmailParam;
import org.crue.hercules.sgi.com.dto.Param;
import org.crue.hercules.sgi.com.dto.Recipient;
import org.crue.hercules.sgi.com.dto.Status;
import org.crue.hercules.sgi.com.model.Attachment;
import org.crue.hercules.sgi.com.model.Email;
import org.crue.hercules.sgi.com.model.EmailAttachmentDeferrable;
import org.crue.hercules.sgi.com.model.EmailParamDeferrable;
import org.crue.hercules.sgi.com.model.EmailParamPK;
import org.crue.hercules.sgi.com.model.EmailRecipientDeferrable;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility class for DTO from/to Entity conversion.
 */
public class TypeConverter {
  private static ModelMapper modelMapper = new ModelMapper();
  static {
    // Email --> EmailOutput
    TypeMap<Email, EmailOutput> emailPropertyMapper = modelMapper.createTypeMap(Email.class, EmailOutput.class);
    emailPropertyMapper.addMapping(src -> {
      if (src.getEmailTpl().getName() != null) {
        return src.getEmailTpl().getName();
      } else {
        return null;
      }
    }, EmailOutput::setTemplate);

    // EmailInput --> Email
    TypeMap<EmailInput, Email> reverseEmailPropertyMapper = modelMapper.createTypeMap(EmailInput.class, Email.class);
    reverseEmailPropertyMapper.addMapping(EmailInput::getTemplate,
        (dest,
            v) -> {
          dest.getEmailTpl().setName((String) v);
        });

    // EmailParam (Entity) --> EmailParam (DTO)
    TypeMap<org.crue.hercules.sgi.com.model.EmailParam, EmailParam> emailParamPropertyMapper = modelMapper
        .createTypeMap(
            org.crue.hercules.sgi.com.model.EmailParam.class, EmailParam.class);
    emailParamPropertyMapper.addMapping(src -> {
      if (src.getParam().getName() != null) {
        return src.getParam().getName();
      } else {
        return null;
      }
    }, EmailParam::setName);

    // EmailParam (DTO) --> EmailParam (Entity)
    TypeMap<EmailParam, org.crue.hercules.sgi.com.model.EmailParam> reverseEmailParamPropertyMapper = modelMapper
        .createTypeMap(EmailParam.class,
            org.crue.hercules.sgi.com.model.EmailParam.class);
    reverseEmailParamPropertyMapper.addMapping(EmailParam::getName,
        (dest,
            v) -> {
          dest.getParam().setName((String) v);
        });
  }

  /**
   * Utility class, can't be instantiated.
   */
  private TypeConverter() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Converts from {@link EmailInput} to {@link Email}.
   * 
   * @param input the {@link EmailInput} to convert
   * @return the converted {@link Email}
   */
  public static Email convert(EmailInput input) {
    return convert(null, input);
  }

  /**
   * Converts from {@link EmailInput} to {@link Email}.
   * 
   * @param id    the {@link Email} id to set
   * @param input the {@link EmailInput} to be converted
   * @return the converted {@link Email}
   */
  public static Email convert(Long id, EmailInput input) {
    Email returnValue = modelMapper.map(input, Email.class);
    returnValue.setId(id);
    EmailAttachmentDeferrable emailAttachmentDeferrable = returnValue.getDeferrableAttachments();
    if (ObjectUtils.isNotEmpty(emailAttachmentDeferrable)) {
      emailAttachmentDeferrable.setId(id);
    }
    EmailRecipientDeferrable emailRecipientDeferrable = returnValue.getDeferrableRecipients();
    if (ObjectUtils.isNotEmpty(emailRecipientDeferrable)) {
      emailRecipientDeferrable.setId(id);
    }
    EmailParamDeferrable emailParamDeferrable = returnValue.getDeferrableParams();
    if (ObjectUtils.isNotEmpty(emailParamDeferrable)) {
      emailParamDeferrable.setId(id);
    }
    return returnValue;
  }

  /**
   * Converts from {@link Email} to {@link EmailOutput}.
   * 
   * @param email the {@link Email} to be converted
   * @return the converted {@link EmailOutput}
   */
  public static EmailOutput convert(Email email) {
    return modelMapper.map(email, EmailOutput.class);
  }

  /**
   * Converts from {@link org.crue.hercules.sgi.com.model.Status} to
   * {@link Status}.
   * 
   * @param status the {@link org.crue.hercules.sgi.com.model.Status} to be
   *               converted
   * @return the converted {@link Status}
   */
  public static Status convert(org.crue.hercules.sgi.com.model.Status status) {
    return modelMapper.map(status, Status.class);
  }

  /**
   * Converts from {@link org.crue.hercules.sgi.com.model.Param} to
   * {@link Param}.
   * 
   * @param param the {@link org.crue.hercules.sgi.com.model.Param} to be
   *              converted
   * @return the converted {@link Param}
   */
  public static Param convert(org.crue.hercules.sgi.com.model.Param param) {
    return modelMapper.map(param, Param.class);
  }

  /**
   * Converts a {@link Page} of {@link org.crue.hercules.sgi.com.model.Param} to a
   * {@link Page} of {@link Param}.
   * 
   * @param page the {@link Page} of {@link org.crue.hercules.sgi.com.model.Param}
   *             to be converted
   * @return the converted {@link Page} of {@link Param}
   */
  public static Page<Param> convertParamPage(Page<org.crue.hercules.sgi.com.model.Param> page) {
    List<Param> content = page.getContent().stream().map(TypeConverter::convert)
        .collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  public static List<org.crue.hercules.sgi.com.model.Recipient> convertRecipients(List<Recipient> recipients) {
    return convertRecipients(null, recipients);
  }

  public static List<org.crue.hercules.sgi.com.model.Recipient> convertRecipients(Long emailId,
      List<Recipient> recipients) {
    if (CollectionUtils.isEmpty(recipients)) {
      return Collections.emptyList();
    }
    return recipients.stream().map(r -> convert(emailId, r))
        .collect(Collectors.toList());
  }

  public static org.crue.hercules.sgi.com.model.Recipient convert(Recipient recipient) {
    return convert(null, recipient);
  }

  public static org.crue.hercules.sgi.com.model.Recipient convert(Long emailId, Recipient recipient) {
    org.crue.hercules.sgi.com.model.Recipient returnValue = modelMapper.map(recipient,
        org.crue.hercules.sgi.com.model.Recipient.class);
    returnValue.setEmailId(emailId);
    return returnValue;
  }

  public static List<Recipient> convertRecipientEntities(List<org.crue.hercules.sgi.com.model.Recipient> recipients) {
    if (CollectionUtils.isEmpty(recipients)) {
      return Collections.emptyList();
    }
    return recipients.stream().map(TypeConverter::convert)
        .collect(Collectors.toList());
  }

  public static Recipient convert(org.crue.hercules.sgi.com.model.Recipient recipient) {
    return modelMapper.map(recipient, Recipient.class);
  }

  public static List<Attachment> convertAttachments(List<String> attachments) {
    return convertAttachments(null, attachments);
  }

  public static List<Attachment> convertAttachments(Long emailId, List<String> attachments) {
    if (CollectionUtils.isEmpty(attachments)) {
      return Collections.emptyList();
    }
    return attachments.stream().map(a -> convert(emailId, a))
        .collect(Collectors.toList());
  }

  public static Attachment convert(String attachment) {
    return convert(null, attachment);
  }

  public static Attachment convert(Long emailId, String attachment) {
    return Attachment.builder().emailId(emailId).documentRef(attachment).build();
  }

  public static List<org.crue.hercules.sgi.com.model.EmailParam> convertEmailParams(List<EmailParam> params) {
    return convertEmailParams(null, params);
  }

  public static List<org.crue.hercules.sgi.com.model.EmailParam> convertEmailParams(Long emailId,
      List<EmailParam> params) {
    if (CollectionUtils.isEmpty(params)) {
      return Collections.emptyList();
    }
    return params.stream().map(p -> convert(emailId, p))
        .collect(Collectors.toList());
  }

  public static org.crue.hercules.sgi.com.model.EmailParam convert(EmailParam param) {
    return convert(null, param);
  }

  public static org.crue.hercules.sgi.com.model.EmailParam convert(Long emailId, EmailParam param) {
    org.crue.hercules.sgi.com.model.EmailParam returnValue = modelMapper.map(param,
        org.crue.hercules.sgi.com.model.EmailParam.class);
    EmailParamPK pk = returnValue.getPk();
    if (pk == null) {
      pk = EmailParamPK.builder().emailId(emailId).build();
    }
    returnValue.setPk(pk);
    return returnValue;
  }

  public static List<EmailParam> convertEmailParamEntities(List<org.crue.hercules.sgi.com.model.EmailParam> params) {
    if (CollectionUtils.isEmpty(params)) {
      return Collections.emptyList();
    }
    return params.stream().map(TypeConverter::convert)
        .collect(Collectors.toList());
  }

  public static EmailParam convert(org.crue.hercules.sgi.com.model.EmailParam param) {
    return modelMapper.map(param, EmailParam.class);
  }

  public static List<String> convertAttachmentEntities(List<Attachment> attachments) {
    if (CollectionUtils.isEmpty(attachments)) {
      return Collections.emptyList();
    }
    return attachments.stream().map(TypeConverter::convert)
        .collect(Collectors.toList());
  }

  public static String convert(Attachment attachment) {
    return attachment.getDocumentRef();
  }
}
