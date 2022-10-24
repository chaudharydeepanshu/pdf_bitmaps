import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'pdf_bitmaps_platform_interface.dart';

/// An implementation of [PdfBitmapsPlatform] that uses method channels.
class MethodChannelPdfBitmaps extends PdfBitmapsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('pdf_bitmaps');

  @override
  Future<int?> pdfPageCount({PDFPageCountParams? params}) async {
    final pageCount = await methodChannel.invokeMethod<int?>(
        'pdfPageCount', params?.toJson());
    return pageCount;
  }

  @override
  Future<Uint8List?> pdfBitmap({PDFBitmapParams? params}) async {
    final Uint8List? bytes = await methodChannel.invokeMethod<Uint8List?>(
        'pdfBitmap', params?.toJson());
    return bytes;
  }

  @override
  Future<List<Uint8List>?> pdfBitmaps({PDFBitmapsParams? params}) async {
    final List? bytesList =
        await methodChannel.invokeMethod<List?>('pdfBitmaps', params?.toJson());
    return bytesList?.cast<Uint8List>();
  }

  @override
  Future<PageSizeInfo?> pdfPageSize({PDFPageSizeParams? params}) async {
    final List? result = await methodChannel.invokeMethod<List?>(
        'pdfPageSize', params?.toJson());
    result?.cast<int>();
    if (result == null) {
      return null;
    } else {
      return PageSizeInfo(
        widthOfPage: result[0] as int,
        heightOfPage: result[1] as int,
      );
    }
  }

  @override
  Future<PdfValidityAndProtection?> pdfValidityAndProtection(
      {PDFValidityAndProtectionParams? params}) async {
    final List? result = await methodChannel.invokeMethod<List?>(
        'pdfValidityAndProtection', params?.toJson());
    result?.cast<List<bool?>>();
    if (result == null) {
      return null;
    } else {
      return PdfValidityAndProtection(
          isPDFValid: result[0], isOpenPasswordProtected: result[1]);
    }
  }

  @override
  Future<String?> cancelBitmaps() async {
    final String? result =
        await methodChannel.invokeMethod<String?>('cancelBitmaps');
    return result;
  }
}

/// Parameters for the [pdfPageCount] method.
class PDFPageCountParams {
  /// Provide path of pdf file page count.
  final String pdfPath;

  /// Create parameters for the [pdfPageCount] method.
  const PDFPageCountParams({required this.pdfPath});

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
    };
  }
}

class BitmapConfigForPage {
  /// Provide pdf page number for which you want bitmap.
  final int pageNumber;

  /// Provide pdf page rotation angle.
  final int rotationAngle;

  /// Provide pdf page bitmap scale from 0.1 to 5.
  final double scale;

  /// Provide pdf page background color.
  final Color backgroundColor;

  BitmapConfigForPage({
    required this.pageNumber,
    this.rotationAngle = 0,
    this.scale = 1,
    this.backgroundColor = Colors.white,
  })  : assert(scale > 0 || scale <= 5,
            'scale should be greater than 0 and less tan or equal to 5'),
        assert(pageNumber > 0, 'pageNumber should be greater than 0');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pageNumber': pageNumber,
      'rotationAngle': rotationAngle,
      'scale': scale,
      'backgroundColor': '#${backgroundColor.value.toRadixString(16)}',
    };
  }

  // Implement toString to make it easier to see information
  // when using the print statement.
  @override
  String toString() {
    return 'PageInfo{pageNumber: $pageNumber, rotationAngle: $rotationAngle, scale: $scale, backgroundColor: $backgroundColor}';
  }
}

/// Parameters for the [pdfBitmap] method.
class PDFBitmapParams {
  /// Provide path of pdf file for bitmap.
  final String pdfPath;

  /// Provide PageInfo for page of pdf for which you want bitmap.
  final BitmapConfigForPage pageInfo;

  /// Create parameters for the [pdfBitmap] method.
  const PDFBitmapParams({
    required this.pdfPath,
    required this.pageInfo,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pageInfo': pageInfo.toJson(),
    };
  }
}

/// Parameters for the [pdfBitmaps] method.
class PDFBitmapsParams {
  /// Provide path of pdf file for bitmap.
  final String pdfPath;

  /// Provide PageInfo List for pages of pdf for which you want bitmap.
  final List<BitmapConfigForPage> pagesInfo;

  /// Create parameters for the [pdfBitmaps] method.
  const PDFBitmapsParams({required this.pdfPath, required this.pagesInfo})
      : assert(pagesInfo.length > 0, 'pagesInfo list cant be empty');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pagesInfo': pagesInfo.map((e) => e.toJson()).toList(),
    };
  }
}

class PageSizeInfo {
  /// Pdf page width.
  final int widthOfPage;

  /// Pdf page height.
  final int heightOfPage;

  PageSizeInfo({
    required this.widthOfPage,
    required this.heightOfPage,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'widthOfPage': widthOfPage,
      'heightOfPage': heightOfPage,
    };
  }

  // Implement toString to make it easier to see information
  // when using the print statement.
  @override
  String toString() {
    return 'PageSizeInfo{widthOfPage: $widthOfPage, heightOfPage: $heightOfPage}';
  }
}

/// Parameters for the [pdfPageSize] method.
class PDFPageSizeParams {
  /// Provide path of pdf file for bitmap.
  final String pdfPath;

  /// Provide PageInfo List for pages of pdf for which you want bitmap.
  final int pageNumber;

  /// Create parameters for the [pdfPageSize] method.
  const PDFPageSizeParams({required this.pdfPath, required this.pageNumber})
      : assert(pageNumber > 0, 'pageNumber should be greater than 0');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pageNumber': pageNumber,
    };
  }
}

class PdfValidityAndProtection {
  /// Is true if pdf is valid.
  final bool? isPDFValid;

  /// Is true if pdf is user/open password protected.
  final bool? isOpenPasswordProtected;

  PdfValidityAndProtection({
    required this.isPDFValid,
    required this.isOpenPasswordProtected,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'isPDFValid': isPDFValid,
      'isOpenPasswordProtected': isOpenPasswordProtected,
    };
  }

  // Implement toString to make it easier to see information
  // when using the print statement.
  @override
  String toString() {
    return 'PdfValidityAndProtection{isPDFValid: $isPDFValid, isOpenPasswordProtected: $isOpenPasswordProtected}';
  }
}

/// Parameters for the [pdfValidityAndProtection] method.
class PDFValidityAndProtectionParams {
  /// Provide path of pdf file which you want validity and protection info.
  final String pdfPath;

  /// Create parameters for the [pdfValidityAndProtection] method.
  const PDFValidityAndProtectionParams({
    required this.pdfPath,
  });

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
    };
  }
}
